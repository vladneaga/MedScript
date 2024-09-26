<template>
    <form ref="formRef" class="signature-pad-form" @submit.prevent="handleSubmitSignature">
      <canvas ref="canvasRef" height="100" width="300" class="signature-pad"></canvas>
  
      <p>
        <a href="#" ref="clearButtonRef" class="clear-button" @click="handleClearButtonEvent">Clear</a>
      </p>
  
      <button ref="submitButtonRef" class="submit-button" type="submit">
        SUBMIT
      </button>
    </form>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    name: 'SignaturePad',
    data() {
      return {
        writingMode: false,
      };
    },
    mounted() {
      this.canvas = this.$refs.canvasRef;
      this.ctx = this.canvas.getContext('2d');
      this.ctx.lineWidth = 3;
      this.ctx.lineJoin = this.ctx.lineCap = 'round';
  
      this.canvas.addEventListener('pointerdown', this.handlePointerDown, { passive: true });
      this.canvas.addEventListener('pointerup', this.handlePointerUp, { passive: true });
      this.canvas.addEventListener('pointermove', this.handlePointerMove, { passive: true });
    },
    methods: {
      async handleSubmitSignature() {
        // Convert the canvas content to a PNG data URL
        const imageUrl = this.canvas.toDataURL('image/png');
        //console.log("Image URL Generated: ", imageUrl); // Debugging
  
        // Convert the data URL to a Blob (binary large object)
        const blob = this.dataURLToBlob(imageUrl);
  
        // Create a FormData object to hold the file data
        const formData = new FormData();
        formData.append('signature', blob, 'signature.png'); // Append the blob with a filename
  
        // Send a POST request to the server to upload the signature
        const token = localStorage.getItem('token'); // Assuming a token is used for authentication
  
        try {
          const response = await axios.post('/physician/uploadSignature', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
              Authorization: `Bearer ${token}`, // Include the token in the request
            },
          });
         // console.log('Signature uploaded successfully:', response.data);
  
          this.clearPad();
  
          // Emit an event to the parent to trigger showTab
          this.$emit('signature-submitted');
        } catch (error) {
          console.error('Error uploading signature:', error);
        }
      },
  
      dataURLToBlob(dataUrl) {
        const byteString = atob(dataUrl.split(',')[1]);
        const mimeString = dataUrl.split(',')[0].split(':')[1].split(';')[0];
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) {
          ia[i] = byteString.charCodeAt(i);
        }
        return new Blob([ab], { type: mimeString });
      },
  
      handleClearButtonEvent(event) {
        event.preventDefault();
        this.clearPad();
      },
  
      clearPad() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      },
  
      handlePointerMove(event) {
        if (!this.writingMode) return;
  
        const [positionX, positionY] = this.getTargetPosition(event);
        this.ctx.lineTo(positionX, positionY);
        this.ctx.stroke();
      },
  
      handlePointerUp() {
        this.writingMode = false;
      },
  
      handlePointerDown(event) {
        this.writingMode = true;
        this.ctx.beginPath();
  
        const [positionX, positionY] = this.getTargetPosition(event);
        this.ctx.moveTo(positionX, positionY);
      },
  
      getTargetPosition(event) {
        const positionX = event.clientX - event.target.getBoundingClientRect().x;
        const positionY = event.clientY - event.target.getBoundingClientRect().y;
  
        return [positionX, positionY];
      },
    },
  };
  </script>
  
  <style>
  :root {
    --primary-color: #000;
  }
  
  body {
    font-family: Arial, Helvetica, sans-serif;
    line-height: 1.4;
    padding: 1rem;
  }
  
  .signature-pad-form {
    max-width: 300px;
    margin: 0 auto;
  }
  
  .signature-pad {
    cursor: url(../../assets/pen.png) 1 26, pointer;
    border: 2px solid var(--primary-color);
    border-radius: 4px;
  }
  
  .clear-button {
    color: var(--primary-color);
  }
  
  .submit-button {
    width: 100%;
    background-color: var(--primary-color);
    border: none;
    padding: 0.5rem 1rem;
    color: #fff;
    cursor: pointer;
    margin-top: 2rem;
  }
  </style>