class SampleController < ApplicationController 
    def list     
        @samplemodels = SampleModel.find(:all)
    end 
    def index
        @samplemodels = SampleModel.all
    end
    def show  
        @samplemodel = SampleModel.find(params[:id])
    end 
    def new  
        @samplemodel = SampleModel.new 
        @samplechilds = SampleChild.find(:all) 
    end 
    def create 
        @samplemodel = SampleModel.new(params[:samplemodel]) 
        if @samplemodel.save 
            redirect_to :action => 'list' # OR -> redirect_to @samplemodel
        else 
            @samplechilds = SampleChild.find(:all) 
            render :action => 'new' # OR ->  render 'new'
        end 
    end 
    def edit 
        @samplemodel = SampleModel.find(params[:id]) 
        @samplechilds = SampleChild.find(:all)
    end 
    def update  
        @samplemodel = SampleModel.find(params[:id]) 
        if @samplemodel.update_attributes(params[:samplemodel]) # OR ->  if @samplemodel.update(samplemodel_params)
            redirect_to :action => 'show', :id => @samplemodel # OR ->  redirect_to @samplemodel  
        else 
            @samplechilds = SampleChild.find(:all) 
            render :action => 'edit' # OR ->  render 'edit'  
        end   
    end 
    def delete 
        SampleModel.find(params[:id]).destroy 
        redirect_to :action => 'list' 
    end
    def destroy
        @samplemodel = SampleModel.find(params[:id])
        @samplemodel.destroy
        redirect_to samplemodels_path
    end

    def show_samplechilds
        @samplechilds = SampleChild.find(params[:id]) 
    end


    private
        def samplemodel_params
            params.require(:samplemodel).permit(:title, :text)
        end

end

# YOU CAN TRY USING THIS CODE SNIPPET FOR EXCEPTION HANDLING ..
"""
begin   
# -   
rescue OneTypeOfException   
# -   
rescue AnotherTypeOfException   
# -   
rescue Exception => exc # HERE'S EXAMPLE FOR HANDLING AN EXCEPTION exc ..
    logger.error('Message for the log file #{exc.message}') 
    flash[:notice] = 'Store error message' 
    redirect_to(:action => 'index') 
else   
# Other exceptions 
ensure 
# Always will be executed 
end
"""

# RUN COMMAND TO CREATE A NEW 'SampleController' -> ruby bin/rails generate controller Sample index
""" THE COMMAND ABOVE WILL AUTO-GENERATE ALL THESE FILES
create  app/controllers/sample_controller.rb
route  get 'sample/index'
invoke  erb
create    app/views/sample
create    app/views/sample/index.html.erb
invoke  test_unit
create    test/controllers/sample_controller_test.rb
invoke  helper
create    app/helpers/sample_helper.rb
invoke    test_unit
invoke  assets
invoke    coffee
create      app/assets/javascripts/sample.coffee
invoke    scss
create      app/assets/stylesheets/sample.scss 
"""

# OR RATHER RUN COMMAND (FOR SCAFFOLDING) -> 
#       ruby script/generate scaffold Sample title:string chef:string instructions:text 
""" THE COMMAND ABOVE WILL AUTO-GENERATE ALL THESE FILES
exists  app/models/ 
exists  app/controllers/ 
exists  app/helpers/ 
create  app/views/recipes 
exists  app/views/layouts/ 
exists  test/functional/ 
exists  test/unit/ 
exists  public/stylesheets/ 
create  app/views/recipes/index.html.erb 
create  app/views/recipes/show.html.erb 
create  app/views/recipes/new.html.erb 
create  app/views/recipes/edit.html.erb 
create  app/views/layouts/recipes.html.erb 
create  public/stylesheets/scaffold.css 
create  app/controllers/recipes_controller.rb 
create  test/functional/recipes_controller_test.rb 
create  app/helpers/recipes_helper.rb 
route  map.resources :recipes 
dependency  model 
exists    app/models/ 
exists    test/unit/ 
exists    test/fixtures/ 
create    app/models/recipe.rb 
create    test/unit/recipe_test.rb 
create    test/fixtures/recipes.yml 
create    db/migrate 
create    db/migrate/20080614192220_create_recipes.rb 
"""
