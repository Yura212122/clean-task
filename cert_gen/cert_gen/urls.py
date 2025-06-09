"""
URL configuration for cert_gen project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin # Module for managing the Django admin panel
from django.urls import path, include # Functions to define URL routes

urlpatterns = [
    # Route for the Django admin panel
    path('admin/', admin.site.urls),
    # Include the URL configurations from the 'create_pdf_cert' app
    path('', include('create_pdf_cert.urls')),  # The main page is managed by 'create_pdf_cert' routes
]
