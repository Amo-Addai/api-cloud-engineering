class CreateAddresses < ActiveRecord::Migration[7.0]
  def change
    create_table :addresses do |t|
      t.string :street1, limit: 255, null: true
      t.string :street2, limit: 255, null: true
      t.string :city, limit: 255, null: true
      t.string :state, limit: 255, null: true
      t.string :zip, limit: 12, null: true
      t.string :country_code, limit: 4, null: true
      t.references :item, null: false, foreign_key: true

      t.timestamps
    end
  end
end
