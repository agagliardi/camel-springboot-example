
# Setup

- input files
mkdir /tmp/sftp-in-foo
cd /tmp/sftp-in-foo
for i in {1..1000}; do echo $RANDOM > $RANDOM.txt; done;

- sftp
docker run -p 2222:22 -d atmoz/sftp foo:pass:::upload

# Run
mvn clean spring-boot:run
