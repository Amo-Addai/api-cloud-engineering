import os
import json


input_file_path = './input.txt'
output_file_path = './output.txt'


def read_input(file_path=input_file_path):
    with open(file_path) as file:
        file_str = file.read()
        return file_str


def write_output(data, file_path=output_file_path):
    with open(file_path, 'a') as file:
        file.write(data)

