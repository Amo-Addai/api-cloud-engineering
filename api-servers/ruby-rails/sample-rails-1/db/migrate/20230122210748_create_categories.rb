class CreateCategories < ActiveRecord::Migration[7.0]
  def change
    create_table :categories do |t|
      t.string :name, limit: 255, null: true, comment: 'Name or title'
      t.string :image_url, limit: 512, null: true, comment: 'Image url'
      t.integer :position, null: true, comment: 'Ordering of list of categories'
      t.boolean :hidden, null: false, default: false, comment: 'Hidden from view'
      t.string :slug, limit: 255, null: true, uniq: true, comment: 'Unique slug'
      t.text :description, limit: 1024, null: true, comment: 'Long description'
      t.string :subtitle, limit: 255, null: true, comment: 'Subtitle'
      t.references :parent, index: true, null: true, foreign_key: { to_table: :categories }, comment: 'Parent category'
      t.timestamps
    end
  end
end
