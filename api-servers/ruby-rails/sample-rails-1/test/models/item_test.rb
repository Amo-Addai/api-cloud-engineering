require "test_helper"

class ItemTest < ActiveSupport::TestCase
  test 'can create' do
    item = Item.create(
      name: 'Missys Cafe',
      description: 'Great restaurant'
    )
    assert item.persisted?
  end
end
