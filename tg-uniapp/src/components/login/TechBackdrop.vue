<template>
  <view class="tech-backdrop" aria-hidden="true">
    <view class="tech-base" />
    <view class="tech-grid" />
    <view class="tech-glow tech-glow-a" />
    <view class="tech-glow tech-glow-b" />
  </view>
</template>

<script lang="ts" setup>
defineOptions({ name: 'TechBackdrop' })
</script>

<style lang="scss" scoped>
.tech-backdrop {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

.tech-base {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 120% 80% at 50% -20%, rgba(56, 189, 248, 0.22), transparent 55%),
    radial-gradient(ellipse 90% 70% at 100% 60%, rgba(99, 102, 241, 0.18), transparent 50%),
    radial-gradient(ellipse 70% 50% at 0% 80%, rgba(6, 182, 212, 0.15), transparent 45%),
    linear-gradient(165deg, #0b1220 0%, #111827 38%, #0f172a 100%);
}

.tech-grid {
  position: absolute;
  inset: 0;
  opacity: 0.35;
  background-image:
    linear-gradient(rgba(56, 189, 248, 0.12) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 189, 248, 0.08) 1px, transparent 1px);
  background-size: 48px 48px, 48px 48px;
  mask-image: radial-gradient(ellipse 80% 70% at 50% 40%, #000 20%, transparent 75%);
}

.tech-glow {
  position: absolute;
  width: 42vmax;
  height: 42vmax;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
}

.tech-glow-a {
  top: -12%;
  right: -8%;
  background: rgba(56, 189, 248, 0.35);
}

.tech-glow-b {
  bottom: -18%;
  left: -12%;
  background: rgba(129, 140, 248, 0.3);
}
</style>
