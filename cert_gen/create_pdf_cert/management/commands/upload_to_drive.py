import os
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload
from google.oauth2.service_account import Credentials
from django.shortcuts import get_object_or_404
from create_pdf_cert.models import Cert
from django.conf import settings


# Authenticate and build the Google Drive service
def authenticate_google_drive():
    file_name = os.path.join(settings.BASE_DIR, 'certificate-generation-445610-55b8a4bc1d6e.json')
    creds = Credentials.from_service_account_file(file_name,
                                                  scopes=['https://www.googleapis.com/auth/drive.file'])
    return build('drive', 'v3', credentials=creds)

# Upload the certificate to the specified Google Drive folder
def upload_to_google_drive(service, file_path, folder_id):
        file_name = os.path.basename(file_path)
        file_metadata = {
            'name': file_name,
            'parents': [folder_id]
        }
        media = MediaFileUpload(file_path, mimetype='application/pdf')
        file = service.files().create(body=file_metadata, media_body=media, fields='id').execute()
        print(f"Uploaded file with ID: {file.get('id')}")
    #print(f"Uploaded file with ID: {file.get('id')}")
        return file.get('id')

def main():
    service = authenticate_google_drive()
    pk = 1
    try:
        instance = get_object_or_404(Cert, pk=pk)

        # try:
        #     instance = Cert.objects.get(pk=pk)
        # except Cert.DoesNotExist:
        #     print(f"No Cert object found with pk={pk}")
        #     return

        file_path = instance.cert.path

        if not os.path.exists(file_path):
            print(f"File not found: {file_path}")
            return

        folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
        file_id = upload_to_google_drive(service, file_path, folder_id)
        print(f"File uploaded successfully: {file_id}")

    except Exception as e:
        print(f"Error: {e}")

    #file_path = f'create_pdf_cert/certificates/{["name"]}_{["course"]}.pdf'
    #folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
    #file_id = upload_to_google_drive(service, file_path, folder_id)
#
# if __name__ == "__main__":
#     main()


    #folder_path = 'create_pdf_cert/certificates/'
    #folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'

    # for file_name in os.listdir(folder_path):
    #     file_path = os.path.join(folder_path, file_name)
    #     if os.path.isfile(file_path):
    #         upload_to_google_drive(service, file_path, folder_id)


#222222222 def upload_certificate(request, pk):
#     instance = get_object_or_404(Cert, pk=pk)
#     file_path = instance.cert.path
#     #file_path = f'cert_gen/create_pdf_cert/certificates/{name}_{course}_{date}.png'
#     folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
#     service = authenticate_google_drive()
#
#     try:
#         file_id = upload_to_google_drive(file_path, folder_id)
#         return JsonResponse({'status': 'success', 'file_id': file_id})
#     except Exception as e:
#         logger.error(f"Error uploading certificate: {e}")
#         return JsonResponse({'status': 'error', 'message': str(e)})
#
#
# def generate_and_upload_certificate(user):
#     file_path = f'create_pdf_cert/certificates/{user["name"]}_{user["course"]}.pdf'
#     generating_file(user, file_path)
#
#     folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
#
#     try:
#         file_id = upload_to_google_drive(file_path, folder_id)
#         return {'status': 'success', 'file_id': file_id}
#     except Exception as e:
#         logger.error(f"Error uploading file to Google Drive: {e}")
#         return {'status': 'error', 'message': str(e)}




