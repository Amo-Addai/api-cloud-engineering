from django.contrib import admin
from .models import Question, Choice, Band, Member, Article, Reporter


class MemberAdmin(admin.ModelAdmin):
    """Customize the look of the auto-generated admin for the Member model"""
    list_display = ('name', 'instrument')
    list_filter = ('band',)


# Register your models here.

admin.site.register(Question)
admin.site.register(Choice)
# 
admin.site.register(Band)  # Use the default options
admin.site.register(Member, MemberAdmin)  # Use the customized options
# 
admin.site.register(Article)
admin.site.register(Reporter)
