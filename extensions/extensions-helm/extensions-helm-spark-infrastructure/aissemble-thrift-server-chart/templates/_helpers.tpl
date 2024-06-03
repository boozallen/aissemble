{{/*
Create the list of ivy dependencies to be installed on the spark history server
*/}}
{{- define "deps.packages.ivy" -}}
{{- if .Values.enable -}}
    {{- range .Values.dependencies.packages }}
        {{- $dep := printf "-dependency, %s," . | replace ":" ", " -}}
        {{ default $dep -}}
    {{- end }}
{{- else -}}
    {{ default "" }}
{{- end -}}
{{- end -}}

{{/*
Create the list of jars to be downloaded directly to the dependencies volume
*/}}
{{- define "deps.jars" -}}
{{- if .Values.enable -}}
    {{- range .Values.dependencies.jars }}
        {{- $jar := printf "%s, " . -}}
        {{ default $jar -}}
    {{- end }}
{{- end -}}
{{- end -}}
