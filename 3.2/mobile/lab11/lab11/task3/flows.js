{
    "annotations": {
      "list": [
        {
          "builtIn": 1,
          "datasource": "-- Grafana --",
          "enable": true,
          "hide": true,
          "iconColor": "rgba(0, 211, 255, 1)",
          "name": "Annotations & Alerts",
          "target": {
            "limit": 100,
            "matchAny": false,
            "tags": [],
            "type": "dashboard"
          }, 
          "type": "dashboard"
        }
      ]
    },
    "editable": true,
    "gnetId": null,
    "graphTooltip": 0,
    "id": 1,
    "links": [],
    "panels": [
      {
        "datasource": null,
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "palette-classic"
            },
            "custom": {
              "axisLabel": "",
              "axisPlacement": "auto",
              "barAlignment": 0,
              "drawStyle": "line",
              "fillOpacity": 0,
              "gradientMode": "none",
              "hideFrom": {
                "legend": false,
                "tooltip": false,
                "viz": false
              },
              "lineInterpolation": "linear",
              "lineWidth": 1,
              "pointSize": 5,
              "scaleDistribution": {
                "type": "linear"
              },
              "showPoints": "auto",
              "spanNulls": false,
              "stacking": {
                "group": "A",
                "mode": "none"
              },
              "thresholdsStyle": {
                "mode": "off"
              }
            },
            "mappings": [],
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {
                  "color": "green",
                  "value": null
                },
                {
                  "color": "red",
                  "value": 80
                }
              ]
            }
          },
          "overrides": []
        },
        "gridPos": {
          "h": 9,
          "w": 12,
          "x": 0,
          "y": 0
        },
        "id": 2,
        "options": {
          "legend": {
            "calcs": [],
            "displayMode": "list",
            "placement": "bottom"
          },
          "tooltip": {
            "mode": "single"
          }
        },
        "targets": [
          {
            "groupBy": [
              {
                "params": [
                  "$__interval"
                ],
                "type": "time"
              },
              {
                "params": [
                  "null"
                ],
                "type": "fill"
              }
            ],
            "measurement": "sense_hat_avg_simulator",
            "orderByTime": "ASC",
            "policy": "default",
            "refId": "A",
            "resultFormat": "time_series",
            "select": [
              [
                {
                  "params": [
                    "temperature"
                  ],
                  "type": "field"
                },
                {
                  "params": [],
                  "type": "mean"
                }
              ]
            ],
            "tags": []
          }
        ],
        "title": "Temperature",
        "type": "timeseries"
      }
    ],
    "schemaVersion": 30,
    "style": "dark",
    "tags": [],
    "templating": {
      "list": []
    },
    "time": {
      "from": "now-5m",
      "to": "now"
    },
    "timepicker": {},
    "timezone": "",
    "title": "Sense Hat simulator",
    "uid": "YOrK8dnnz",
    "version": 2
}