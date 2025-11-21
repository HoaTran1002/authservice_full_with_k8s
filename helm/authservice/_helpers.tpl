{{- define "authservice.name" -}}
authservice
{{- end -}}
{{- define "authservice.fullname" -}}
{{- printf "%s" (include "authservice.name" .) -}}
{{- end -}}
{{- define "authservice.secretName" -}}
auth-secrets
{{- end -}}
