{{- define "repo-scorer.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "repo-scorer.fullname" -}}
{{- printf "%s-%s" .Release.Name (include "repo-scorer.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
