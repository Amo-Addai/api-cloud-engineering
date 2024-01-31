require "application_system_test_case"

class ItemsTest < ApplicationSystemTestCase
  setup do
    @item = items(:one)
  end

  test "visiting the index" do
    visit items_url
    assert_selector "h1", text: "Items"
  end

  test "should create item" do
    visit items_url
    click_on "New item"

    fill_in "Description", with: @item.description
    fill_in "Book url", with: @item.book_url
    fill_in "Cuisine", with: @item.cuisine
    fill_in "Custom dates", with: @item.custom_dates
    fill_in "End date", with: @item.end_date
    fill_in "Image url", with: @item.image_url
    fill_in "Menu url", with: @item.menu_url
    fill_in "Phone", with: @item.phone
    fill_in "Price quartile", with: @item.price_quartile
    fill_in "Reservation url", with: @item.reservation_url
    fill_in "Slug", with: @item.slug
    fill_in "Special notes", with: @item.special_notes
    fill_in "Start date", with: @item.start_date
    fill_in "Title", with: @item.title
    fill_in "Venue name", with: @item.venue_name
    fill_in "Website url", with: @item.website_url
    click_on "Create Item"

    assert_text "Item was successfully created"
    click_on "Back"
  end

  test "should update Item" do
    visit item_url(@item)
    click_on "Edit this item", match: :first

    fill_in "Description", with: @item.description
    fill_in "Book url", with: @item.book_url
    fill_in "Cuisine", with: @item.cuisine
    fill_in "Custom dates", with: @item.custom_dates
    fill_in "End date", with: @item.end_date
    fill_in "Image url", with: @item.image_url
    fill_in "Menu url", with: @item.menu_url
    fill_in "Phone", with: @item.phone
    fill_in "Price quartile", with: @item.price_quartile
    fill_in "Reservation url", with: @item.reservation_url
    fill_in "Slug", with: @item.slug
    fill_in "Special notes", with: @item.special_notes
    fill_in "Start date", with: @item.start_date
    fill_in "Title", with: @item.title
    fill_in "Venue name", with: @item.venue_name
    fill_in "Website url", with: @item.website_url
    click_on "Update Item"

    assert_text "Item was successfully updated"
    click_on "Back"
  end

  test "should destroy Item" do
    visit item_url(@item)
    click_on "Destroy this item", match: :first

    assert_text "Item was successfully destroyed"
  end
end
