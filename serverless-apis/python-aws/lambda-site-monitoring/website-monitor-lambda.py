import boto3
import os
from datetime import datetime
from urllib.request import Request, urlopen


# * Uncomment for local execution
# temp_sites = ["https://www.amazon.com", "https://www.ebay.com"]
# temp_site_checks = ["amazon", "ebay"]

TEMP_SITES = os.environ['sites']
TEMP_SITES_CHECK = os.environ['site_check']
TEMP_SNS_ARN = os.environ['sns_arn']


def validate(res):
    EXPECTED = [
        item
        for item
        in TEMP_SITES_CHECK.split(',')
        if item
    ]
    for check in EXPECTED:
        if check in res:
            return_value = True
            break
    return return_value


def send_message_to_sns(message):
    sns = boto3.client('sns')
    response = sns.publish(
        TopicArn=TEMP_SNS_ARN,
        Message=message,
        Subject='Site Monitor'
    )
    print(response)


def check_sites(event, context):
    SITES = [
        item
        for item
        in TEMP_SITES.split(',')
        if item
    ]
    all_sites_reachable = True
    
    for site in SITES:
        NOW = str(datetime.now())
        print('Checking ', site)
        try:
            req = Request(site, headers={'User-Agent': 'AWS Lambda'})
            if not validate(str(urlopen(req).read())):
                print('SNS call from try section')
        except:
            all_sites_reachable = False
            print('Check failed')
            print('Call from the except section')
            send_message_to_sns('Check failed for ', + site + ' at ' + NOW)
        else:
            print('Check passed!', + NOW)
            print('Call from the else section')
        finally: print('End of loop run at: ' + NOW)
    
    if all_sites_reachable: send_message_to_sns('All sites are reachable')


def lambda_handler(event, context):
    check_sites(event, context)