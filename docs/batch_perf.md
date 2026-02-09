2026-02-08T19:18:51.302+09:00  INFO 34799 --- [nova-server] [         task-1] c.n.n.d.b.service.CardNewsBatchService   : CardNews batch started at 2026-02-08T19:18:51.301976753
2026-02-08T19:22:34.751+09:00  INFO 34799 --- [nova-server] [         task-1] c.n.n.d.b.service.CardNewsBatchService   : CardNews batch completed: saved=69/69

69개 저장 3분 43초 소요

    "jobName": "articleIngestionJob",
      "executionId": 5,
      "status": "FAILED",
      "exitCode": "FAILED",
      "startTime": "2026-02-09T16:15:55.784511",
      "endTime": "2026-02-09T16:18:12.225121",
      "createTime": "2026-02-09T16:15:55.712387",
      "steps": [
        {
          "stepName": "articleIngestionStep-DeepSearch",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:55.898843",
          "endTime": "2026-02-09T16:15:57.961015"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:55.924802",
          "endTime": "2026-02-09T16:15:59.500123"
        },
        {
          "stepName": "articleIngestionStep-HackerNews",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 695,
          "writeCount": 641,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:55.94856",
          "endTime": "2026-02-09T16:18:12.142045"
        },
        {
          "stepName": "articleIngestionStep-StackExchange",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 2,
          "writeCount": 2,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:55.966252",
          "endTime": "2026-02-09T16:15:59.471943"
        },
        {
          "stepName": "articleIngestionStep-NewsAPI",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 100,
          "writeCount": 100,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:55.98665",
          "endTime": "2026-02-09T16:15:59.473961"
        },
        {
          "stepName": "articleIngestionStep-GitHub",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 12,
          "writeCount": 12,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.017363",
          "endTime": "2026-02-09T16:15:59.190744"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.039403",
          "endTime": "2026-02-09T16:15:58.76797"
        },
        {
          "stepName": "articleIngestionStep-DevTo",
          "status": "FAILED",
          "exitCode": "FAILED",
          "readCount": 0,
          "writeCount": 0,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.065695",
          "endTime": "2026-02-09T16:15:58.425787"
        },
        {
          "stepName": "articleIngestionStep-NaverNewsAPI",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 60,
          "writeCount": 60,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.0778",
          "endTime": "2026-02-09T16:15:59.56181"
        },
        {
          "stepName": "articleIngestionStep-GNews",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.119558",
          "endTime": "2026-02-09T16:15:59.502187"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:56.160179",
          "endTime": "2026-02-09T16:16:03.414313"
        },
        {
          "stepName": "articleIngestionStep-NewsData",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:57.930285",
          "endTime": "2026-02-09T16:15:59.6518"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:57.956654",
          "endTime": "2026-02-09T16:16:02.175171"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:58.250706",
          "endTime": "2026-02-09T16:16:02.583228"
        },
        {
          "stepName": "articleIngestionStep-TechBlog",
          "status": "COMPLETED",
          "exitCode": "COMPLETED",
          "readCount": 10,
          "writeCount": 10,
          "skipCount": 0,
          "startTime": "2026-02-09T16:15:58.463667",
          "endTime": "2026-02-09T16:16:01.379926"
        }
      ]

startTime "2026-02-09T16:15:55.784511"
endTime "2026-02-09T16:18:12.225121"
905개 수집하는데 2분 17초 소요