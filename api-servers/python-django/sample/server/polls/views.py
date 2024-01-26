from django.shortcuts import render
from django.http import HttpResponse
from django.db import models
from django import forms
from django.contrib.auth.decorators import login_required
from django.utils.translation import ugettext
# 
from .models import Question, Choice, Band, Member, Article, Reporter

# Create your views here.

def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")

def sample_route(request):
    return HttpResponse("Sample Route Response ..") 

def homepage(request):
    """
    Shows the homepage with a welcome message that is translated in the
    user's language.
    """
    message = ugettext('Welcome to our site!')
    return render(request, 'homepage.html', {'message': message})

# 

@login_required
def my_protected_view(request):
    """A view that can only be accessed by logged-in users"""
    return render(request, 'protected.html', {'current_user': request.user})

# 

def band_listing(request):
    """A view of all bands."""
    bands = models.Band.objects.all()
    return render(request, 'bands/band_listing.html', {'bands': bands})

def member_listing(request):
    """A view of all members."""
    members = models.Member.objects.all()
    return render(request, 'members/member_listing.html', {'members': members})

# 

def year_archive(request, year):
    a_list = Article.objects.filter(pub_date__year=year)
    context = {'year': year, 'article_list': a_list}
    return render(request, 'news/year_archive.html', context)

def article_detail(request, year, month, pk):
    pass

# 
class BandContactForm(forms.Form):
    subject = forms.CharField(max_length=100)
    message = forms.CharField()
    sender = forms.EmailField()
    cc_myself = forms.BooleanField(required=False)
