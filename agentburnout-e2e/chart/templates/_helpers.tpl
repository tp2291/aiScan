
{{- define "agentburnout-e2e.name"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default .Chart.Name .Values.nameOverride -}}
{{- $gpre := default "" $global.namePrefix -}}
{{- $pre := default "" .Values.env.optional.appPrefix -}}
{{- $suf := default "" .Values.nameSuffix -}}
{{- $gsuf := default "" $global.nameSuffix -}}
{{- $name := print $gpre $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" -}}
{{- end -}}

{{- define "agentburnout-e2e.jobname" -}}
{{ template "agentburnout-e2e.name" . }}job
{{- end -}}

{{- define "agentburnout-e2e.fullname"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name .Values.fullnameOverride -}}
{{- $env := default "" (print .Values.environmentPrefix "-") | trimPrefix "<nil>-" -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix "-") | trimPrefix "<nil>-" -}}
{{- $suf := default ""  .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $env $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-e2e.jobselector"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix ) | trimPrefix "<nil>" -}}
{{- $name := print $pre $base -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-e2e.labels.standard" -}}
app: {{ template "agentburnout-e2e.name" . }}
job: {{ template "agentburnout-e2e.jobselector" . }}
chart: {{ template "agentburnout-e2e.chartref" . }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
version: {{ .Chart.Version }}
{{- end -}}

{{- define "agentburnout-e2e.chartref" -}}
{{- replace "+" "_" .Chart.Version | printf "%s-%s" .Chart.Name | trunc 63 -}}
{{- end -}}
