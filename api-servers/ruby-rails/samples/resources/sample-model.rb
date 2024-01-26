class SampleModel < ActiveRecord::Base 
    has_many :samplemodelchilds
end

# RUN THIS COMMAND TO CREATE A NEW MODEL ->
#  ruby bin/rails generate model SampleModel title:string text:text
