class SampleModelChild < ActiveRecord::Base 
    belongs_to :samplemodel
    validates_presence_of :title 
    validates_numericality_of :price, :message=>"Some Error Message" 
end