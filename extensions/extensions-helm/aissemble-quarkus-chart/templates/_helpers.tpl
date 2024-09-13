{{/*
aiSSEMBLE Foundation::aiSSEMBLE Configuration Store Chart
Copyright (C) 2021 Booz Allen
*/}}

{{/* vim: set filetype=mustache: */}}
{{/*
Renders volumes that specified under toggleConfiguration.
Usage:
{{ include "toggleable.configuration.volumes" . }}
*/}}
{{- define "toggleable.configuration.volumes" -}}
  {{- with .Values.toggleableConfiguration }}
    {{- if and .toggle .volumes }}
      {{- toYaml .volumes }}
    {{- end }}
  {{- end }}
{{- end -}}

{{/* vim: set filetype=mustache: */}}
{{/*
Renders volumeMounts that specified under toggleConfiguration.
Usage:
{{ include "toggleable.configuration.volumeMounts" . }}
*/}}
{{- define "toggleable.configuration.volumeMounts" -}}
  {{- with .Values.toggleableConfiguration }}
    {{- if and .toggle .volumeMounts}}
      {{- toYaml .volumeMounts }}
    {{- end }}
  {{- end }}
{{- end -}}

{{/* vim: set filetype=mustache: */}}
{{/*
Renders volumeMounts that specified under toggleConfiguration.
Usage:
{{ include "toggleable.configuration.configMap" . }}
*/}}
{{- define "toggleable.configuration.configMap" -}}
  {{- with .Values.toggleableConfiguration }}
    {{- if and .toggle .configMap }}
      {{- range $property := .configMap.supplementalQuarkusConfig }}
        {{- $property | nindent 6}}
      {{- end }}
    {{- end }}
  {{- end }}
{{- end -}}



