pipeline {
    agent {
        label "Linux_Default || masterLin"
    }
    options {
        buildDiscarder(logRotator(
                artifactDaysToKeepStr: '1',
                artifactNumToKeepStr: '10',
                daysToKeepStr: '1',
                numToKeepStr: '10'))
        skipDefaultCheckout()
        timeout(30)
    }
    parameters {
        booleanParam name: 'haproxy', description: 'Развернуть конфиг', defaultValue: false
        choice name: 'environment',
                choices: ['',
                          ''],
                description: 'Среда развертывания'
        choice name: 'type',
                choices: ['lol',
                          'wtf',],
                description: 'Тип интеграции'
        
    }
    stages {
        stage('Download scripts') {
            steps {
                script {
                    dir('pipelines') {
                        checkout scm
                    }
                }
            }
        }
        stage('Deploy applications') {
            steps {
                script {
                    dir('pipelines/ansible') {
                        def environment = ""
                        switch (params.environment) {
                            case "Приемочно-сдаточные испытания (ПСИ)":
                                environment = "test"
                                break
                            case "Продакшн (ПРОД)":
                                environment = "prod"
                                break
                        }
                        def type = ""
                        switch (params.type) {
                            case "lol":
                                type = "lol"
                                break
                            case "wtf":
                                type = "wtf"
                                break    
                        }
                        def hosts = "";
                        if (params.haproxy) {
                            hosts += "haproxy-" + type
                        def tags = hosts
                                {                                   
                                    ansiblePlaybook(
                                            installation: 'ansible26',
                                            colorized: true,
                                            playbook: 'playbook.yml',
                                            inventory: environment + '/hosts',
                                            extras: "--limit '${hosts}' -vvv",
                                            
                                    )    
                        }                     

                    }
                }
            }
        }
    }
}
}

