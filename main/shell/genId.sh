#根据输入的游戏ID和用户ID来生成对应的ID，用于mahout生成推荐结果
#输入的格式为 {game_id},{user_id}
#输出格式为{game_id},{user_id},{gen_game_numid},{gen_user_numid}
#!/bin/awk
BEGIN{a=0;b=0}
{
if($1 in m){}else{m[$1]=a++};
if($2 in n){}else{n[$2]=b++};
r[NR,1]=$1;
r[NR,2]=$2
}

END{
for(x=1;x<=NR;x++){
print r[x,1]","r[x,2]","m[r[x,1]]","n[r[x,2]];
}
}
