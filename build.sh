#!/bin/bash
cd yapp-auth
./build.sh
cd ..
cd yapp-gateway
./build.sh
cd ..
cd yapp-integration
./build.sh
cd ..
cd yapp-messaging
./build.sh
