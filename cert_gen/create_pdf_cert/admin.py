from django.contrib import admin
from .models import Cert, MailSettings, GiftCert

admin.site.register(MailSettings)

@admin.register(Cert)
class CertAdmin(admin.ModelAdmin):
    list_filter = ('date', 'course',)
    list_display = ('name', 'course', 'date',)

@admin.register(GiftCert)
class GiftCertAdmin(admin.ModelAdmin):
    """Admin configuration for gift certificates"""
    list_filter = ('expiry_date', 'course',)
    list_display = ('course', 'expiry_date', 'cert_number', 'created')
    ordering = ('-expiry_date',)
    search_fields = ('course', 'cert_number')
    date_hierarchy = 'expiry_date'
