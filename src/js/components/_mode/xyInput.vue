<template>
  <div class="input-box-cell">
    <text class="desc">{{title}}</text>
    <input :type="type" :placeholder="placeholder" :max-length="20" class="desc-input" :value="value" @input="oninput"/>
    <text class="other">{{other}}</text>
  </div>
</template>
<script>
export default {
  props:{
    title:{
      type:String,
      default:""
    },
    maxLength:{
      type:Number,
      default:100
    },
    type:{
      type:String,
      default:"text"
    },
    value:{
      type:String,
      default:""
    },
    placeholder:{
      type:String,
      default:""
    },
    other:{
      type:String,
      default:""
    }
  },
  methods:{
    oninput(e){
      this.$emit("input",e.value);
    }
  }
};
</script>
<style scoped>
.input-box-cell {
  flex-direction: row;
  justify-content: flex-start;
  margin-bottom: 40;
  align-items: center;
}

.desc {
  font-size: 24;
  color: #000000;
  margin-right: 20;
  width: 120;
  text-align: right;
}
.desc-input {
  width: 245;
  height: 60;
  border-color: #a6a4a1;
  border-style: solid;
  border-width: 1;
  border-radius: 50;
  text-align: center;
   font-size: 26px;
}
.other{
  font-size: 18;
  margin-left: 5;
}
</style>
