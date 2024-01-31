class CreateItems < ActiveRecord::Migration[7.0]
  def change
    create_table :items do |t|
      t.string :name, null: true, limit: 255, comment: 'Title or name'
      t.text :description, null: true, comment: 'Body or description'
      t.string :image_url, null: true, limit: 512, comment: 'Image url'
      t.string :website_url, null: true, limit: 512, comment: 'Website url'
      t.string :reservation_url, null: true, limit: 512, comment: 'Reservation url'
      t.string :book_url, null: true, limit: 512, comment: 'Book ticket url'
      t.string :menu_url, null: true, limit: 512, comment: 'Menu url'
      t.string :phone, null: true, limit: 64, comment: 'Phone number'
      t.string :venue_name, null: true, limit: 255, comment: 'Name of the venue'
      t.date :start_date, null: true, comment: 'Start of the event'
      t.date :end_date, null: true, comment: 'End of the event'
      t.string :special_notes, null: true, limit: 255, comment: 'Additional special notes'
      t.date :custom_dates, array: true, comment: 'Custom days that the event is running'
      t.string :slug, null: true, uniq: true, limit: 255
      t.integer :price_quartile, comment: 'How expensive something is'
      t.integer :cuisine, comment: 'What type of cuisine'

      t.timestamps
    end
  end
end
