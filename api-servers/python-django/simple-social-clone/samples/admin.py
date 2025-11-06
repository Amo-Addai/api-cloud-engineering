from django.contrib import admin
from . import models

class GroupMemberInline(admin.Tabularline):
    model = models.GroupMember

admin.site.register(models.Group)
