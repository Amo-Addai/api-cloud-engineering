class SampleMigration < ActiveRecord::Migration 
    
    def self.up
        create_table :samplemodels do |t| 
            t.column :title, :string, :limit => 32, :null => false 
            t.column :price, :float 
            t.column :subject_id, :integer 
            t.column :description, :text 
            t.column :created_at, :timestamp 
        end 
    end 
   
    def self.down
        drop_table :samplemodels 
    end 

    def change # THIS IS THE NEW (v 5.0) WAY OF SPECIFYING t's PROPS IN MIGRATIONS ..
        create_table :samplemodels do |t|
            t.string :title
            t.text :text
            t.timestamps
        end
    end

end 

# RUN COMMAND TO EXECUTE MIGRATIONS ->  rake db:migrate  OR  ruby bin/rails db:migrate