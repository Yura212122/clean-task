from django.urls import path
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('generating_file', views.generating_file, name='generating_file'),
    path('download/<int:pk>/', views.download_file, name='download_file'),
    path('clear_history', views.clear_history, name='clear_history'),
    path('run-task/', views.run_task, name='run_task'),
    path('email-tracker/', views.email_tracker, name='email-tracker'),
    path('generate_gift_cert', views.generate_gift_cert, name='generate_gift_cert'),
    path('download_gift/<int:pk>/', views.download_gift_cert, name='download_gift_cert'),
    path('clear_gift_history', views.clear_gift_history, name='clear_gift_history'),

]
