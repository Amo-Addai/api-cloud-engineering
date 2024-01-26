from flask import Flask, render_template
import os


def create_app(test_config=None):
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY='dev',
        DATABASE=os.path.join(app.instance_path, 'flask_sqlite_database.sqlite'),
    )

    if test_config is None:
        # load the instance config, if it exists, when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # load the test config if passed in
        app.config.from_mapping(test_config)

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    @app.route('/')
    def index():
        return 'Index Page'

    @app.route('/hello')
    def hello_world():
        return 'Hello, World'

    @app.route('/static')
    def static(): # '/static' MUST BE A FOLDER IN THIS DIRECTORY
        return redirect(url_for('static', filename='style.css'))

    @app.route('/client', methods=['GET'])
    def client(client=None): # client IS THE FRONT-END FRAMEWORK FOLDER eg. angular
        clientPath =  client + '/client.html'  # OR JUST PUT ALL .html FILES IN 'templates' DIRECTORY
        return render_template(clientPath, client=client)  # RENDERS clientPath WITHIN THE 'templates' DIRECTORY ..

    return app


create_app()  # NOW, RUN THE APP'S FACTORY FUNCTION ..
