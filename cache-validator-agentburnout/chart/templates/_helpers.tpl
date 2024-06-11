
{{- define "cache-validator-agentburnout.name"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default .Chart.Name .Values.nameOverride -}}
{{- $gpre := default "" $global.namePrefix -}}
{{- $pre := default "" .Values.namePrefix -}}
{{- $suf := default "" .Values.nameSuffix -}}
{{- $gsuf := default "" $global.nameSuffix -}}
{{- $name := print $gpre $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" -}}
{{- end -}}

{{- define "cache-validator-agentburnout.fullname"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name .Values.fullnameOverride -}}
{{- $env := default "" (print .Values.environmentPrefix "-") | trimPrefix "<nil>-" -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix "-") | trimPrefix "<nil>-" -}}
{{- $suf := default ""  .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $env $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "cache-validator-agentburnout.labels.standard" -}}
app: {{ template "cache-validator-agentburnout.name" . }}
chart: {{ template "cache-validator-agentburnout.chartref" . }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
version: {{ .Chart.Version }}
{{- end -}}

{{- define "cache-validator-agentburnout.chartref" -}}
{{- replace "+" "_" .Chart.Version | printf "%s-%s" .Chart.Name | trunc 63 -}}
{{- end -}}


{{- define "cache-validator-agentburnout.deploymentName" }}
{{- ( include "cache-validator-agentburnout.fullname" . ) -}}
{{- end -}}



{{- define "cache-validator-agentburnout.replicaCount"}}
{{- $outer := . -}}
{{- print $outer.Values.env.required.replicaCountForDefaultDeployment -}}
{{- end -}}
