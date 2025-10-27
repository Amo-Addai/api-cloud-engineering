
import time
import os
import random

global_random_val = random.random()

def cold_start(event, context):
    local_random_val = random.random()
    print(local_random_val)
    print(global_random_val)

def simple_types(event, context):
    print(event)
    return event

def list_types(event, context):
    print(event)
    student_scores = {
        "john": 100,
        "bob": 90,
        "bharath": 100,
    }
    scores = []
    for name in event:
        scores.append(student_scores[name])
    return scores

def dict_types(event, context):
    john_scores = event["john"]
    for score in john_scores:
        print(score)
    return event
    
def list_dict_types(event, context):
    list_scores = []
    for name in event:
        scores = event[name]
        for score in scores:
            print(score)
            list_scores.append(score)
    return list_scores

def lambda_handler(event, context):
    print("Lambda function ARN:", context.invoked_function_arn)
    # todo: print out more context .info ..
    # .log_stream_name, log_group_name, aws_request_id, memory_limit, get_remaining_time

