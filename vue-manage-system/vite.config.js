import vue from '@vitejs/plugin-vue'

module.exports =  {
    base: './',
    plugins: [vue()],
    optimizeDeps: {
        include: ['schart.js']
    },
    server:{
        proxy: {
            // 如果是 /lsbdb 打头，则访问地址如下
            //'/design': 'https://jim.jx.cn/design/basic/list',
            // 如果是 /lsbdb 打头，则访问地址如下
            // '/lsbdb': {
            //   target: 'http://10.192.195.96:8087/',
            //   changeOrigin: true,
            //   // rewrite: path => path.replace(/^\/lsbdb/, '')
            // }
            '/api': {
                //本地服务接口地址
                target: 'http://localhost:8081/',
                //远程演示服务地址,可用于直接启动项目
                //target: 'https://www.jim.jx.cn/',
                ws: true,
                changeOrigin:true,
                rewrite:path=>{
                    console.log(path)
                    return path.replace(/^\/api/,'')
                }
            },

            /*百度翻译，前端直接调用*/
            /*'/transform': {
                //本地服务接口地址
                target: 'http://api.fanyi.baidu.com/api/trans/vip/translate',
                //远程演示服务地址,可用于直接启动项目
                //target: 'https://www.jim.jx.cn/',
                ws: true,
                changeOrigin:true,
                rewrite:path=>{
                    console.log(path)
                    return path.replace(/^\/transform/,'')
                }
            }*/
        }
    }

}
