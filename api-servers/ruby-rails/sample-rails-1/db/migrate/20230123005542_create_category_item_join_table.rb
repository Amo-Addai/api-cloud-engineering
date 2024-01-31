class CreateCategoryItemJoinTable < ActiveRecord::Migration[7.0]
  def change
    create_join_table :categories, :items do |t|
      t.index :category_id
      t.index :item_id
    end
  end
end
