require "test_helper"

class CategoryTest < ActiveSupport::TestCase
  test 'can create' do
    category = Category.create(
      name: 'Cafes',
    )
    assert category.persisted?
  end
end
