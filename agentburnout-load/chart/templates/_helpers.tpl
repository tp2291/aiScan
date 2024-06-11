
{{- define "agentburnout-load.name"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default .Chart.Name .Values.nameOverride -}}
{{- $gpre := default "" $global.namePrefix -}}
{{- $pre := default "" .Values.env.optional.appPrefix -}}
{{- $suf := default "" .Values.nameSuffix -}}
{{- $gsuf := default "" $global.nameSuffix -}}
{{- $name := print $gpre $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" -}}
{{- end -}}

{{- define "agentburnout-load.jobname" -}}
{{ template "agentburnout-load.name" . }}job
{{- end -}}

{{- define "agentburnout-load.fullname"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name .Values.fullnameOverride -}}
{{- $env := default "" (print .Values.environmentPrefix "-") | trimPrefix "<nil>-" -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix "-") | trimPrefix "<nil>-" -}}
{{- $suf := default ""  .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $env $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-load.jobselector"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix ) | trimPrefix "<nil>" -}}
{{- $name := print $pre $base -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-load.labels.standard" -}}
app: {{ template "agentburnout-load.name" . }}
job: {{ template "agentburnout-load.jobselector" . }}
chart: {{ template "agentburnout-load.chartref" . }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
version: {{ .Chart.Version }}
{{- end -}}

{{- define "agentburnout-load.chartref" -}}
{{- replace "+" "_" .Chart.Version | printf "%s-%s" .Chart.Name | trunc 63 -}}
{{- end -}}
