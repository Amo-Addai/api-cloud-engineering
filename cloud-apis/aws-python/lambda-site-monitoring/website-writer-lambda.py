import boto3
import os
import datetime


S3_BUCKET = os.environ.get('BUCKET_NAME')


def htmlify(
    subject='Page render error.', 
    message='An error occurred during page rendering.'
):
    return f"""
        <html>
            <body>
                <h1>{subject}</h1>
                <br/>
                {message}
            </body>
        </html>
    """


def lambda_handler(event, context):
    session = boto3.Session()
    file_content = ''
    response_object = ''
    for key in event['Records']:
        file_content = file_content + str(key)
        if 'Check failed' in file_content:
            messagePosition1 = file_content.index('Check failed')
            messagePosition2 = file_content.index('Timestamp') - 7
            file_content = file_content[messagePosition1:messagePosition2]
            response_object = htmlify("Unreachable website detected: ", file_content + 'UTC')
        else:
            response_object = htmlify('Website Check Complete: ', 'All configured websites are reachable at ' \
            + str(datetime.datetime.now()) + 'UTC')
    s3 = session.resource('s3')
    object = s3.Object(S3_BUCKET, 'index.html')
    object.put(Body=response_object, ContentType='text/html')