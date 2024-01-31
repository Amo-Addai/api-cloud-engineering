require "test_helper"

class AddressTest < ActiveSupport::TestCase
  test 'can create' do
    item = Item.create(
      name: 'Missys Cafe',
      description: 'Great restaurant'
    )
    address = Address.create(
      street1: '123 Fake St',
      city: 'Arlington',
      state: 'VA',
      zip: '22201',
      item: item
    )

    assert address.persisted?
  end

  test 'can not create without item' do
    address = Address.new(
      street1: '123 Fake St',
      city: 'Arlington',
      state: 'VA',
      zip: '22201'
    )

    assert_not address.valid?
    assert_equal 1, address.errors.size
    err = address.errors.first
    assert_equal :item, err.attribute
    assert_equal :blank, err.type
  end
end
