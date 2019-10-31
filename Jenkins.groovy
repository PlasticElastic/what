pipeline {
    agent any
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
        booleanParam name: 'haproxy', description: 'deploy conf', defaultValue: false
        choice name: 'environment',
                choices: ['test',
                          'prod'],
                description: 'deployment enviroment'
        choice name: 'type',
                choices: ['http',
                          'tcp',],
                description: 'type of integration'
        
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
                            case "test":
                                environment = "test"
                                break
                            case "prod":
                                environment = "prod"
                                break
                        }
                        def type = ""
                        switch (params.type) {
                            case "http":
                                type = "http"
                                break
                            case "tcp":
                                type = "tcp"
                                break    
                        }
                        def hosts = "";
                        if (params.haproxy) {
                            hosts = "haproxy-" + type
                        def tags = hosts
                                {                                   
                                    ansiblePlaybook(
                                            installation: 'ansible26',
                                            colorized: true,
                                            playbook: 'playbook.yml',
                                            inventory: 'hosts',
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

