json.extract! item, :id, :name, :description, :image_url, :website_url, :reservation_url, :book_url, :menu_url, :phone, :venue_name, :start_date, :end_date, :special_notes, :custom_dates, :slug, :price_quartile, :cuisine, :formatted_start_date, :formatted_end_date, :formatted_custom_dates, :created_at, :updated_at
json.address { json.partial! 'items/address', address: item.address } if item&.address
json.url item_url(item, format: :json)
