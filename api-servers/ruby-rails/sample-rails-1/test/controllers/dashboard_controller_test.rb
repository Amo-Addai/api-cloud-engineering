require "test_helper"

class DashboardControllerTest < ActionDispatch::IntegrationTest
  include Devise::Test::IntegrationHelpers

  test 'should not get index' do
    get dashboard_index_url
    assert_redirected_to :new_user_session
  end

  test 'should get index if signed in' do
    sign_in users(:admin)
    get dashboard_index_url
    assert_response :success
  end
end
