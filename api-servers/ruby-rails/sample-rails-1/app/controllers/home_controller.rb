class HomeController < ApplicationController
  def index
    if user_signed_in?
      return redirect_to dashboard_index_path
    end
  end
end
