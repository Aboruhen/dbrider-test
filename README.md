# liquibase-demo
Few liquibase features that can help 

* [Project Help](HELP.md)


### Dockerization

#### Colima
`colima stop`

`colima delete`

`colima start --cpu 4 --memory 8 --mount $HOME:w`
`colima start --arch x86_64 --cpu 4 --memory 8 --mount $HOME:w`

for Oracle image for mac M1
`colima start --cpu 4 --memory 8 --mount $HOME:w --arch x86_64`

with kubernetes
`colima start --cpu 4 --memory 8 --mount $HOME:w --with-kubernetes`

for testcontainers: Customization specific for Testcontainers
`echo 'export DOCKER_HOST=unix://$HOME/.colima/docker.sock' >> ~/.bash_profile`