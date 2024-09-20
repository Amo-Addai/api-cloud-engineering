import {
    Queue,
    QueueScheduler,
    Worker,
    Job
} from 'bullmq'
import Redis from 'ioredis'
import winston from 'winston'
import nodemailer from 'nodemailer'
import os from 'os' // * confirm not 'require'd


// * 1 - Define the Job Queue and Concurrency Control


// create redis connection
const redisConnection = new Redis()

// initialize job queue with concurrency control and job prioritization
const batchQueue =
    new Queue(
        'batchQueue',
        {
            connection: redisConnection,
            defaultJobOptions: {
                attempts: 3, // retries
                backoff: { // retry with exponential backoff
                    type: 'exponential',
                    delay: 5000
                },
                priority: 2 // default priority, lower number = higher priority
            }
        }
    )

// scheduler for delayed and periodic jobs
const scheduler =
    new QueueScheduler(
        'batchQueue',
        { connection: redisConnection }
    )

// worker to process jobs with concurrency control (eg. max 5 jobs running at once)
const worker =
    new Worker(
        'batchQueue',
        async job => (
            console.log(`Processing job with id ${job.id}`),
            // simulate job processing
            await performBatchJob(job),
            'Job completed successfully'
        ),
        {
            concurrency: 5, // concurrency control
            connection: redisConnection
        }
    )


// * 2 - Adding Jobs to the Queue


// add a job to the queue with specific priority
const addJobToQueue =
    async (
        jobData,
        priority = 2
    ) => (
        await batchQueue.add(
            'batchJob',
            jobData,
            { priority }
        ),
        console.log(`Added job to queue with priority ${priority}`)
    )

// adding sample jobs
[
    { type: 'reportGeneration', data: {} },
    { type: 'dataBackup', data: {} },
    { type: 'etlProcess', data: {} }
].forEach((job, i) => addJobToQueue(job, i)) // higher index number, lower-priority job


// * 3 - Processing Jobs


// batch-processing logic for different batch types
const performBatchJob =
    async (
        job,
        switchObj = {}
    ) => (
        switchObj = {
            'reportGeneration':
                await generateReport(job.data), // optional-chain not required; key - job?.data?.type
            'dataBackup':
                await backupData(job.data),
            'etlProcess':
                await etlProcess(job.data)
        },
        switchObj[job?.data?.type]
        || (
            console.log(`Error processing ${job?.id || '-'}:`),
            console.log(`Unknown job type: ${job?.data?.type || '-'}`),
            null
        ),
        console.log(`Job ${job?.id || '-'} processed successfully`)
    )

// example implementation of job types
const generateReport = 
    async data => (
        console.log('Generating report...'),
        // todo: simulate report generation instead of an empty promise
        await new Promise(resolve => setTimeout(resolve, 2000))
    )

const backupData =
    async data => (
        console.log('Backing up data...'),
        // todo: simulate data backup
        await new Promise(resolve => setTimeout(resolve, 3000))
    )

const etlProcess =
    async data => (
        console.log('Running ETL process...'),
        // todo: simulate ETL process
        await new Promise(resolve => setTimeout(resolve, 4000))
    )


// * 4 - Retry Mechanism & Error Handling


/*
retry mechanism is already built into BullMQ via .attempts
can add more granular error handling inside the job functions
if a job fails, it'll be retried with exponential backoff
can also send alerts (eg. email) if jobs fail after all retries exhausten
*/

worker.on(
    'failed',
    async (job, error) => (
        console.error(
            `Job ${job?.id || '-'} failed after ${job?.attemptsMade || '-'} attempts: ${error}`
        ),
        // send an alert (eg. email) when a job fails compleletely
        await sendFailureAlert(job, err)
    )
)

// sample email notification using nodemailer
const sendFailureAlert =
    async (
        job, error,
        transporter = null,
        mailOptions = null
    ) => (
        transporter =
            nodemailer.createTransport({
                service: 'gmail',
                auth: {
                    user: process.env.SENDER_EMAIL || 'test@email.com',
                    pass: process.env.SENDER_EMAIL_PASSWORD || 'pass'
                }
            }),
        mailOptions = {
            from: process.env.SENDER_EMAIL || 'test@email.com',
            to: process.env.RECEIVER_EMAIL || 'test@email.com',
            subject: `Job ${job?.id || '-'} Failed`,
            text: `Job ${job?.id || '-'} failed with error: ${error?.message || '-'}`
        },
        await transporter.sendMail(mailOptions),
        console.log(`Failure alert sent for job ${job?.id || '-'}`)
    )


// * 5 - Logging


// set up winston logger - only for logging worker-processes' info, to log-file './jobs-logs.log'
const logger =
    winston.createLogger({
        level: 'info',
        format: winston.format.combine(
            winston.format.timestamp(),
            winston.format.json()
        ),
        transports: [
            new winston.transports.File(
                { filename: 'job-logs.log' }
            ),
            new winston.transports.Console()
        ]
    })

// log job start & completion
worker.on(
    'active',
    job =>
        logger.info( // todo: ensure job?.id so '-' isn't logged
            `Job ${job?.id || '-'} is now running.`
        )
)

worker.on(
    'completed',
    (job, result) =>
        logger.info(
            `Job ${job?.id || '-'} completed with result ${result}`
        )
)


// * 6 - Resource Monitoring and Throttling


/*
to avoid overloading the system, monitor CPU and memory usage.
if resource limits are exceeded, pause the worker queue until resources are freed.
*/

const monitorResources = (
    cpuLoad = null,
    freeMem = null
) => (
    cpuLoad = os.loadavg()[0], // 1-minute load average
    freeMem = os.freemem(),
    (
        cpuLoad > 2.0
        || 
        freeMem < 100 * 1024 * 1024
    ) ? ( // thresholds for pausing
        console.log('Resource limits exceeded. Pausing the job queue'),
        worker.pause()
    ) : worker.resume()
)

// monitor resources every 10 seconds
setInterval(monitorResources, 10000)


// * 7 - Job Prioritization and Scheduling


// schedule a job to run in 1 hour
batchQueue.add(
    'batchJob',
    { type: 'reportGeneration', data: {} },
    { delay: 3600 * 1000 }
)


// * 8 - Graceful Shutdown


/*
to ensure Orchestrator shuts down gracefully (eg. during redeployment or server-restart),
implement 'graceful-shutdown' mechanism that waits for active jobs to complete before shutting down
*/

const gracefulShutdown =
    async () => (
        console.log('Shutting down gracefully...'),
        await worker.close(), // waits for active jobs to complete
        redisConnection.quit(),
        console.log('Shutdown complete')
    )

process.on('SIGINT', gracefulShutdown) // SIGINT (Signal Interrupt) - Ctrl+C manual cli-shutdown
process.on('SIGTERM', gracefulShutdown) // SIGTERM (Signal Terminate) - sent by the system or an orchestrator
