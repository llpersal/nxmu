(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[35],{"4IyT":function(e,a,t){"use strict";var l=t("g09b");Object.defineProperty(a,"__esModule",{value:!0}),a.default=void 0,t("IzEo");var s=l(t("bx4M"));t("7Kak");var d=l(t("9yH6")),n=t("Y2fQ"),r=l(t("q1tI")),u=t("M0Ur"),o=l(t("/v1c")),i=l(t("WD6q")),f=function(e){var a=e.dropdownGroup,t=e.salesType,l=e.loading,f=e.salesPieData,c=e.handleChangeSalesType;return r.default.createElement(s.default,{loading:l,className:i.default.salesCard,bordered:!1,title:r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.analysis.the-proportion-of-sales",defaultMessage:"The Proportion of Sales"}),style:{height:"100%"},extra:r.default.createElement("div",{className:i.default.salesCardExtra},a,r.default.createElement("div",{className:i.default.salesTypeRadio},r.default.createElement(d.default.Group,{value:t,onChange:c},r.default.createElement(d.default.Button,{value:"all"},r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.channel.all",defaultMessage:"ALL"})),r.default.createElement(d.default.Button,{value:"online"},r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.channel.online",defaultMessage:"Online"})),r.default.createElement(d.default.Button,{value:"stores"},r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.channel.stores",defaultMessage:"Stores"})))))},r.default.createElement("div",null,r.default.createElement("h4",{style:{marginTop:8,marginBottom:32}},r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.analysis.sales",defaultMessage:"Sales"})),r.default.createElement(u.Pie,{hasLegend:!0,subTitle:r.default.createElement(n.FormattedMessage,{id:"dashboardandanalysis.analysis.sales",defaultMessage:"Sales"}),total:function(){return r.default.createElement(o.default,null,f.reduce(function(e,a){return a.y+e},0))},data:f,valueFormat:function(e){return r.default.createElement(o.default,null,e)},height:248,lineWidth:4})))},c=f;a.default=c}}]);