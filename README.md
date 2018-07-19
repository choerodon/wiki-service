# Wiki Service
Wiki Service is responsible for establishing communication with [XWiki](https://www.xwiki.org), handling XWiki related logic and forwarding it to other services.

## Feature
`Wiki Service` contains features as follows:
- User synchronization
- Role synchronization
- Organization synchronization
- Project synchronization
- Create space

## Requirements
- Java8
- [Iam Service](https://github.com/choerodon/iam-service)
- [MySQL](https://www.mysql.com)
- [Kafka](https://kafka.apache.org)

## Installation and Getting Started
1. init database

    ```sql
    CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
    CREATE DATABASE wiki_service DEFAULT CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON wiki_service.* TO choerodon@'%';
    FLUSH PRIVILEGES;
    ```
1. run command `sh init-local-database.sh`
1. run command as follow or run `WikiServiceApplication` in IntelliJ IDEA

    ```bash
    mvn clean spring-boot:run
    ```

## Dependencies
- `go-register-server`: Register server
- `config-server`：Configure server
- `kafka`
- `mysql`: wiki_service database

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the  [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
