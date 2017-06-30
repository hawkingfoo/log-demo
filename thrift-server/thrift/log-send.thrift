namespace java thrift


service LogServer {
    string getLogRes(1:string log);
}