require "test_helper"

class CategoriesControllerTest < ActionDispatch::IntegrationTest
  include Devise::Test::IntegrationHelpers

  setup do
    @category = categories(:one)
    sign_in users(:admin)
  end

  test 'should not get index if not signed in' do
    sign_out :user
    get dashboard_index_url
    assert_redirected_to :new_user_session
  end

  test 'should get index if signed in' do
    get dashboard_index_url
    assert_response :success
  end

  test "should get new" do
    get new_category_url
    assert_response :success
  end

  test "should create category" do
    assert_not Category.exists?(slug: 'unique-slug')
    assert_difference("Category.count") do
      post categories_url, params: { category: { description: @category.description, hidden: @category.hidden, name: @category.name, position: @category.position, slug: 'unique-slug', subtitle: @category.subtitle, image_url: @category.image_url } }
    end

    assert_redirected_to category_url('unique-slug')
  end

  test "should show category" do
    get category_url(@category)
    assert_response :success
  end

  test "should get edit" do
    get edit_category_url(@category)
    assert_response :success
  end

  test "should update category" do
    patch category_url(@category), params: { category: { description: @category.description, hidden: @category.hidden, name: @category.name, position: @category.position, slug: @category.slug, subtitle: @category.subtitle, image_url: @category.image_url } }
    assert_redirected_to category_url(@category)
  end

  test "should destroy category" do
    assert_equal 1, @category.subtypes.count
    assert_difference("Category.count", -2) do
      delete category_url(@category)
    end

    assert_redirected_to categories_url
  end
end
