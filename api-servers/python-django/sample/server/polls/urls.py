from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('sample/route', views.sample_route, name='sample_route'), 
    path('homepage', views.homepage, name='homepage'),
    path('articles/<int:year>/', views.year_archive),
    path('articles/<int:year>/<int:month>/<int:pk>/', views.article_detail),
]
