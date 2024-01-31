json.extract! category, :id, :name, :image_url, :position, :hidden, :slug, :description, :subtitle, :subtypes, :items, :created_at, :updated_at
json.url category_url(category, format: :json)
