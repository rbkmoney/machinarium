#!groovy
build('machinarium', 'java-maven') {
    checkoutRepo()
    loadBuildUtils()

    def mvnArgs = '-DjvmArgs="-Xmx256m"'
    runStage('Maven package') {
        withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
                if (env.BRANCH_NAME == 'master') {
                    sh 'mvn deploy --batch-mode --settings  $SETTINGS_XML ' + "${mvnArgs}"
                } else {
                    sh 'mvn package --batch-mode --settings  $SETTINGS_XML ' + "${mvnArgs}"
                }
        }
    }
}