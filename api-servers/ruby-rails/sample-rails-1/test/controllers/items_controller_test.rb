require "test_helper"

class ItemsControllerTest < ActionDispatch::IntegrationTest
  include Devise::Test::IntegrationHelpers
  setup do
    @item = items(:one)
    sign_in users(:admin)
  end

  test 'should not get index if not signed in' do
    sign_out :user
    get dashboard_index_url
    assert_redirected_to :new_user_session
  end

  test "should get index" do
    get items_url
    assert_response :success
  end

  test "should get new" do
    get new_item_url
    assert_response :success
  end

  test "should create item" do
    assert_difference("Item.count") do
      post items_url, params: { item: { name: @item.name, description: @item.description, book_url: @item.book_url, cuisine: @item.cuisine, custom_dates: @item.custom_dates, end_date: @item.end_date, image_url: @item.image_url, menu_url: @item.menu_url, phone: @item.phone, price_quartile: @item.price_quartile, reservation_url: @item.reservation_url, special_notes: @item.special_notes, start_date: @item.start_date, venue_name: @item.venue_name, website_url: @item.website_url } }
    end

    assert_redirected_to item_url(Item.last)
  end

  test "should show item" do
    get item_url(@item)
    assert_response :success
  end

  test "should get edit" do
    get edit_item_url(@item)
    assert_response :success
  end

  test "should update item" do
    patch item_url(@item), params: { item: { name: @item.name, description: @item.description, book_url: @item.book_url, cuisine: @item.cuisine, custom_dates: @item.custom_dates, end_date: @item.end_date, image_url: @item.image_url, menu_url: @item.menu_url, phone: @item.phone, price_quartile: @item.price_quartile, reservation_url: @item.reservation_url, slug: @item.slug, special_notes: @item.special_notes, start_date: @item.start_date, venue_name: @item.venue_name, website_url: @item.website_url } }
    assert_redirected_to item_url(@item)
  end

  test "should destroy item" do
    assert_difference("Item.count", -1) do
      delete item_url(@item)
    end

    assert_redirected_to items_url
  end
end
