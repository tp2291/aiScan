
{{- define "agentburnout-api.name"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default .Chart.Name .Values.nameOverride -}}
{{- $gpre := default "" $global.namePrefix -}}
{{- $pre := default "" .Values.namePrefix -}}
{{- $suf := default "" .Values.nameSuffix -}}
{{- $gsuf := default "" $global.nameSuffix -}}
{{- $name := print $gpre $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" -}}
{{- end -}}

{{- define "agentburnout-api.fullname"}}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name .Values.fullnameOverride -}}
{{- $env := default "" (print .Values.environmentPrefix "-") | trimPrefix "<nil>-" -}}
{{- $pre := default "" (print .Values.env.optional.appPrefix "-") | trimPrefix "<nil>-" -}}
{{- $suf := default ""  .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $env $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-api.servicename" }}
{{- $global := default (dict) .Values.global -}}
{{- $base := default "" .Chart.Name .Values.fullnameOverride -}}
{{- $pre := default ""  (print .Values.appPrefix "-") | trimPrefix "<nil>-" -}}
{{- $ver := default ""  (print "-" .Chart.Version ) | replace "." "-" -}}
{{- $suf := default "-svc" .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $pre $base $suf $gsuf $ver -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}

{{- define "agentburnout-api.labels.standard" -}}
app: {{ template "agentburnout-api.name" . }}
chart: {{ template "agentburnout-api.chartref" . }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
version: {{ .Chart.Version }}
{{- end -}}

{{- define "agentburnout-api.chartref" -}}
{{- replace "+" "_" .Chart.Version | printf "%s-%s" .Chart.Name | trunc 63 -}}
{{- end -}}


{{- define "agentburnout-api.deploymentName" }}
{{- ( include "agentburnout-api.fullname" . ) -}}
{{- end -}}



{{- define "agentburnout-api.replicaCount"}}
{{- $outer := . -}}
{{- print $outer.Values.env.required.replicaCount -}}
{{- end -}}


{{- define "agentburnout-api.api-service-host" -}}
{{- $global := default (dict) .Values.global -}}
{{- $base := "-api" -}}
{{- $gpre := default "" $global.appnamePrefix -}}
{{- $pre := default "" .Values.env.optional.appPrefix -}}
{{- $suf := default "" .Values.fullnameSuffix -}}
{{- $gsuf := default "" $global.fullnameSuffix -}}
{{- $name := print $gpre $pre $base $suf $gsuf -}}
{{- $name | lower | trunc 54 | trimSuffix "-" | trimPrefix "-" -}}
{{- end -}}