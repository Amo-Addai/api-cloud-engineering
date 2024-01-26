
Rails.application.routes.draw do
    # bin/rails generate controller Welcome index <- THIS COMMAND AUTO-GENERATES get 'welcome/index'
    get 'welcome/index' # tells Rails to map requests to http://localhost:3000/welcome/index to the welcome controller's index action
    root 'welcome#index' # tells Rails to map requests to the root of the application to the welcome controller's index action
   
    resources :articles # tells Rails that there'll be CRUD operations or 'articles'
    # THEREFORE, THIS WILL BE HANDLED BY AN AUTO-GENERATED 'articles_controller.rb' FILE
    # BUT THE CONTROLLER ITSELF WILL BE CALLED ArticlesController (IT'LL BE EMPTY SO DEFINE IT'S METHODS)
    # BUT ONLY IF YOU RUN THE COMMAND -> bin/rails generate controller Articles
    # METHODS / CRUD OPERATIONS TO BE DEFINED -> list, show, new, create, edit, update, delete
    # BUT YOU CAN ALSO CREATE YOUR OWN CUSTOM METHODS WITHIN THE CONTROLLER IF YOU PREFER ..

    # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end

# RUN COMMAND TO SEE ALL DEFINED ROUTES -> ruby bin/rails routes
