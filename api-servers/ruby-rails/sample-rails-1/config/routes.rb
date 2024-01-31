Rails.application.routes.draw do
  root to: 'home#index'
  get 'home/index'

  get 'dashboard/index'

  devise_for :users, controllers: { registrations: 'registrations' }
  resources :categories
  resources :items
end
