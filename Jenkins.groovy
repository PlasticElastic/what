                                {                                   
                                    ansiblePlaybook(
                                            installation: 'ansible26',
                                            colorized: true,
                                            playbook: 'playbook.yml',
                                            inventory: environment + '/hosts',
                                            extras: "--limit '${hosts}' -vvv",
                                            
                                    )    
                        }   
